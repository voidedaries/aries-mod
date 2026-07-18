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


$newVersion = [Microsoft.VisualBasic.Interaction]::InputBox(
@"
Aries Version Update

Use:
MAJOR.MINOR.PATCH[-STAGE]

Examples:
  0.1.0-alpha
  0.1.0-alpha.1
  0.2.0
  1.0.0

Version guidelines:
  MAJOR  → Breaking changes, rewrites, major redesigns
  MINOR  → New features or large additions
  PATCH  → Bug fixes, improvements, tweaks
  STAGE  → Optional release stage (alpha, beta, release)

Before committing:
✓ Increase the version if needed
✓ Keep unchanged only for small commits
✓ Never downgrade versions


Previous version:
$previousVersion

New version:
"@,
    "Aries Version Check",
    $currentVersion
)


if ([string]::IsNullOrWhiteSpace($newVersion)) {
    $newVersion = $currentVersion
}


Write-Host ""
Write-Host "Previous version: $previousVersion"
Write-Host "Current version:  $newVersion"
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