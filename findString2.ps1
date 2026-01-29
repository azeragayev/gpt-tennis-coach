# GPTennisCoach

cls
$rootFolder = "C:\myFiles\DDD\AI-lab\08-AndroidApp\GPTennisCoach\app\src\main\java\com\aaa\gptenniscoach\"
$searchPath = $rootFolder + "*.kt"
Write-Host "`n=== GPTennisCoach ===:" 

$fs1 = Get-ChildItem -Path $searchPath -Recurse 
$fs = $fs1 | Sort-Object name -Unique

$i = 0
foreach ($f in $fs){
    $fsp = ($f.PSPath).replace($rootFolder, "")
    $fsp2 = $fsp.Replace("Microsoft.PowerShell.Core\FileSystem::", "")
    $i++
    Write-Host $i, $fsp2
}

Write-Host "`n=== GPTennisCoach. 'Copy' files"
$fsp3 = @()
foreach ($f in $fs){
    $fsp = ($f.PSPath).replace($rootFolder, "")
    $fsp2 = $fsp.Replace("Microsoft.PowerShell.Core\FileSystem::", "")
    if ($fsp2.Contains(" - Copy")) {
        $fsp3+=$fsp2
    }
}

$fsp4 = $fsp3 | sort

$i = 0
foreach ($f in $fsp4){
        $i++
        $fsp3+=$fsp2
        Write-Host $i, $f
}



Write-Host "`nDONE"

