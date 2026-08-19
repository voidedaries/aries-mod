Add-Type -AssemblyName PresentationFramework
Add-Type -AssemblyName Microsoft.VisualBasic

$file = "gradle.properties"

function Get-VersionFromFile($path) {
    return (Select-String -Path $path -Pattern "^mod_version=").Line.Split("=")[1]
}

function Get-VersionNumber($version) {
    # Extract only MAJOR.MINOR.PATCH
    return [version]([regex]::Match($version, "\d+\.\d+\.\d+").Value)
}

$currentVersion = Get-VersionFromFile $file

$previousVersion = git show HEAD:$file 2>$null |
    Select-String "^mod_version=" |
    ForEach-Object {
        $_.Line.Split("=")[1]
    }

if (-not $previousVersion) {
    $previousVersion = $currentVersion
}

# Extract the base MAJOR.MINOR.PATCH from the current version
$currentBaseVersion = [regex]::Match(
    $currentVersion,
    "\d+\.\d+\.\d+"
).Value

# Create version selection window
[xml]$xaml = @"
<Window
    xmlns="http://schemas.microsoft.com/winfx/2006/xaml/presentation"
    Title="Aries Version Check"
    Width="430"
    Height="390"
    WindowStartupLocation="CenterScreen"
    ResizeMode="NoResize">

    <StackPanel Margin="25">

        <TextBlock
            Text="Aries Version Update"
            FontSize="20"
            FontWeight="Bold"
            Margin="0,0,0,15"/>

        <TextBlock
            Text="Select release type:"
            FontWeight="SemiBold"
            Margin="0,0,0,8"/>

        <RadioButton
            Name="ReleaseRadio"
            Content="Release"
            IsChecked="True"
            Margin="0,3"/>

        <RadioButton
            Name="BetaRadio"
            Content="Beta"
            Margin="0,3"/>

        <RadioButton
            Name="AlphaRadio"
            Content="Alpha"
            Margin="0,3"/>

        <TextBlock
            Text="Version:"
            FontWeight="SemiBold"
            Margin="0,15,0,5"/>

        <TextBox
            Name="VersionBox"
            Text="$currentBaseVersion"
            Height="28"
            Padding="5"/>

        <TextBlock
            Name="PreviousVersionText"
            Text="Previous version: $previousVersion"
            Foreground="Gray"
            Margin="0,12,0,0"/>

        <StackPanel
            Orientation="Horizontal"
            HorizontalAlignment="Right"
            Margin="0,20,0,0">

            <Button
                Name="CancelButton"
                Content="Cancel"
                Width="80"
                Margin="0,0,8,0"
                IsCancel="True"/>

            <Button
                Name="ContinueButton"
                Content="Continue"
                Width="90"
                IsDefault="True"/>

        </StackPanel>

    </StackPanel>

</Window>
"@

$reader = New-Object System.Xml.XmlNodeReader $xaml
$window = [Windows.Markup.XamlReader]::Load($reader)

$releaseRadio = $window.FindName("ReleaseRadio")
$betaRadio = $window.FindName("BetaRadio")
$alphaRadio = $window.FindName("AlphaRadio")
$versionBox = $window.FindName("VersionBox")
$continueButton = $window.FindName("ContinueButton")
$cancelButton = $window.FindName("CancelButton")

$cancelled = $false

$cancelButton.Add_Click({
    $script:cancelled = $true
    $window.Close()
})

$continueButton.Add_Click({
    $window.Close()
})

$window.ShowDialog() | Out-Null

if ($cancelled) {
    Write-Host "Version update cancelled."
    exit 1
}

# Get the version entered by the user
$baseVersion = $versionBox.Text.Trim()

if ([string]::IsNullOrWhiteSpace($baseVersion)) {
    [System.Windows.MessageBox]::Show(
        "Version cannot be empty.",
        "Aries Version Error",
        "OK",
        "Error"
    )

    exit 1
}

# Validate MAJOR.MINOR.PATCH
if ($baseVersion -notmatch "^\d+\.\d+\.\d+$") {
    [System.Windows.MessageBox]::Show(
        "Invalid version format:`n`n$baseVersion`n`nExpected format: MAJOR.MINOR.PATCH",
        "Aries Version Error",
        "OK",
        "Error"
    )

    exit 1
}

# Determine release type
if ($alphaRadio.IsChecked) {
    $releaseType = "alpha"
}
elseif ($betaRadio.IsChecked) {
    $releaseType = "beta"
}
else {
    $releaseType = "release"
}

# Build final version
if ($releaseType -eq "release") {
    $newVersion = $baseVersion
}
else {
    $newVersion = "$baseVersion-$releaseType"
}

Write-Host ""
Write-Host "Previous version: $previousVersion"
Write-Host "Selected type:    $releaseType"
Write-Host "New version:      $newVersion"
Write-Host ""

# Check downgrade
try {
    if ((Get-VersionNumber $newVersion) -lt (Get-VersionNumber $previousVersion)) {

        [System.Windows.MessageBox]::Show(
            "Version cannot be lower than previous version!`n`nPrevious: $previousVersion`nCurrent: $newVersion",
            "Aries Version Error",
            "OK",
            "Error"
        )

        exit 1
    }
}
catch {
    [System.Windows.MessageBox]::Show(
        "Invalid version format:`n$newVersion",
        "Aries Version Error",
        "OK",
        "Error"
    )

    exit 1
}

# Confirm version
if ($newVersion -eq $previousVersion) {

    $result = [System.Windows.MessageBox]::Show(
        "Version unchanged ($newVersion)`n`nCommit with same version?",
        "Aries Version Check",
        "YesNo",
        "Question"
    )

    if ($result -ne "Yes") {
        Write-Host "Commit cancelled."
        exit 1
    }

}
else {

    $result = [System.Windows.MessageBox]::Show(
        "Version changed:`n`n$previousVersion -> $newVersion`n`nCommit with this version?",
        "Aries Version Check",
        "YesNo",
        "Question"
    )

    if ($result -ne "Yes") {
        Write-Host "Commit cancelled."
        exit 1
    }
}

# Update gradle.properties
(Get-Content $file) `
    -replace "^mod_version=.*", "mod_version=$newVersion" |
    Set-Content $file

git add $file

Write-Host ""
Write-Host "Version set to $newVersion"

exit 0