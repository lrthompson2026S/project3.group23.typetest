
$project = $PSScriptRoot

Start-Process -FilePath "cmd.exe" `
    -ArgumentList "/k", "gradlew.bat run --debug-jvm --console=plain" `
    -WorkingDirectory $project

# Wait until Gradle's Java application is ready for debugging
$timer = [Diagnostics.Stopwatch]::StartNew()

while ($timer.Elapsed.TotalSeconds -lt 60) {
    if (Get-NetTCPConnection -LocalPort 5005 `
        -State Listen -ErrorAction SilentlyContinue) {
        exit 0
    }

    Start-Sleep -Milliseconds 250
}

Write-Error "Debug JVM did not start on port 5005"
exit 1
