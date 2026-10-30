# SmartServe JavaFX Application Runner
$java = "C:\Program Files\Java\jdk-26.0.2\bin\java.exe"
$m2 = "C:\Users\NEHA KUMARI\.m2\repository"

$modulePath = "$m2\org\openjfx\javafx-controls\21.0.2\javafx-controls-21.0.2-win.jar;" +
              "$m2\org\openjfx\javafx-controls\21.0.2\javafx-controls-21.0.2.jar;" +
              "$m2\org\openjfx\javafx-graphics\21.0.2\javafx-graphics-21.0.2-win.jar;" +
              "$m2\org\openjfx\javafx-graphics\21.0.2\javafx-graphics-21.0.2.jar;" +
              "$m2\org\openjfx\javafx-base\21.0.2\javafx-base-21.0.2-win.jar;" +
              "$m2\org\openjfx\javafx-base\21.0.2\javafx-base-21.0.2.jar;" +
              "$m2\org\openjfx\javafx-fxml\21.0.2\javafx-fxml-21.0.2-win.jar;" +
              "$m2\org\openjfx\javafx-fxml\21.0.2\javafx-fxml-21.0.2.jar"

$cp = "target/classes;" +
      "$m2\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar"

Write-Host "Syncing resources and building sources..."
& powershell -ExecutionPolicy Bypass -File .\build_and_sync.ps1

Write-Host "Launching SmartServe JavaFX Application..."
& $java --module-path $modulePath --add-modules javafx.controls,javafx.fxml -cp "$cp;$modulePath" com.smartcanteen.Main
