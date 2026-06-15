$base = "C:\Users\basti\Desktop\Smartlogix"
$modules = @{
    "ms-envios" = "com.smartlogix.envios"
    "ms-pedidos" = "com.smartlogix.pedidos"
    "ms-transportistas" = "com.smartlogix.transportistas"
    "ms-logistics-base" = "com.smartlogix.logisticsbase"
}

foreach ($module in $modules.Keys) {
    $modulePath = Join-Path $base $module
    if (-not (Test-Path $modulePath)) {
        Write-Host "SKIP missing module $module"
        continue
    }

    $pkg = $modules[$module]
    foreach ($srcType in @('main', 'test')) {
        $oldRoot = Join-Path $modulePath "src\$srcType\java\ms_logistics_base"
        if (Test-Path $oldRoot) {
            $pkgPath = $pkg -replace '\.', '\\'
            $newRoot = Join-Path $modulePath "src\$srcType\java\$pkgPath"
            New-Item -ItemType Directory -Path (Split-Path $newRoot) -Force | Out-Null
            if (-not (Test-Path $newRoot)) {
                Move-Item -Path $oldRoot -Destination $newRoot
                Write-Host "MOVED $oldRoot -> $newRoot"
            } elseif ($oldRoot -ne $newRoot) {
                Move-Item -Path $oldRoot -Destination $newRoot
                Write-Host "MOVED $oldRoot -> $newRoot"
            }
        } else {
            Write-Host "No old root for $module $srcType"
        }
    }

    Get-ChildItem -Path $modulePath -Filter *.java -Recurse | ForEach-Object {
        $content = Get-Content -Raw -Encoding UTF8 $_.FullName
        $newContent = [regex]::Replace($content, '^(package|import)\s+ms_logistics_base', { param($m) "$($m.Groups[1].Value) $pkg" }, [System.Text.RegularExpressions.RegexOptions]::Multiline)
        if ($newContent -ne $content) {
            Set-Content -Path $_.FullName -Value $newContent -Encoding UTF8
            Write-Host "UPDATED $($_.FullName.Substring($base.Length + 1))"
        }
    }
}
Write-Host 'REFATOR COMPLETE'
