$javac = "C:\Program Files\Java\jdk-26.0.2\bin\javac.exe"
$java = "C:\Program Files\Java\jdk-26.0.2\bin\java.exe"
$m2 = "C:\Users\NEHA KUMARI\.m2\repository"

$cp = "target/classes;target/test-classes;" +
      "$m2\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar"

& powershell -ExecutionPolicy Bypass -File .\build_and_sync.ps1

New-Item -ItemType Directory -Force -Path target/test-classes | Out-Null
$testFiles = Get-ChildItem -Recurse -Filter *.java src/test/java | Select-Object -ExpandProperty FullName
& $javac -cp $cp -d target/test-classes $testFiles

if ($LASTEXITCODE -eq 0) {
    & $java -cp $cp com.smartcanteen.RegistrationFlowTest
} else {
    Write-Host "Test compilation failed!"
}
