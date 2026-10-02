# Silent-SOS PowerShell Runner
Set-Location $PSScriptRoot
Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "  SILENT-SOS: COVERT EMERGENCY RESPONSE SYSTEM (JAVA 21)" -ForegroundColor Green
Write-Host "  B.Tech Engineering Microproject" -ForegroundColor Yellow
Write-Host "=================================================================" -ForegroundColor Cyan

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] Java is not installed or not in your PATH." -ForegroundColor Red
    exit 1
}

Write-Host "[1/3] Preparing directories..." -ForegroundColor Gray
if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }
if (-not (Test-Path "data")) { New-Item -ItemType Directory -Path "data" | Out-Null }

Write-Host "[2/3] Compiling Java 21 sources..." -ForegroundColor Cyan
$sources = Get-ChildItem -Path "src/main/java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
& javac -d bin -sourcepath src/main/java $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[3/3] Starting Silent-SOS server on http://localhost:8080 ..." -ForegroundColor Green
& java -cp bin com.silentsos.Main
