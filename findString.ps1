# GPTennisCoach

#cls
$searchString = "knee"

Write-Host "`n=== GPTennisCoach: Search for $searchString. === `n" 

$fs1 = Get-ChildItem -Path "C:\myFiles\DDD\AI-lab\08-AndroidApp\GPTennisCoach\app\src\main\java\com\aaa\gptenniscoach\*.kt" -Recurse | 
Select-String -Pattern $searchString

$fs = $fs1 | Sort-Object path -Unique


$i = 0
foreach ($f in $fs){
    $fsp = ($f.path).replace("C:\myFiles\DDD\AI-lab\08-AndroidApp\GPTennisCoach\app\src\main\java\com\aaa\gptenniscoach\", "")
    if ($fsp.Contains(" - Copy")) {continue}
    $i++
    Write-Host $i, $fsp
}
Write-Host "`nDONE"

