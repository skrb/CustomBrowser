package custombrowser;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.async.Task;
import javafx.async.TaskEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextBox;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CustomBrowser extends Application {
    private TextBox box;
    private WebEngine engine;
    private WebView view;
    
    public static void main(String[] args) {
        Application.launch(CustomBrowser.class, args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Custom Browser");
        
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600);

        // 垂直方向にレイアウトするコンテナ
        VBox vbox = new VBox(10);
        vbox.setLayoutY(10);

        // 水平方向にレイアウトするコンテナ
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        
        // テキスト入力
        box = new TextBox(40);
        hbox.getChildren().add(box);
        
        // ボタン
        Button button = new Button("Load");
        button.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // テキストボックスから取得した文字列を
                // WebEngine でロードする
                String url = box.getText();
                engine.load(url);
            }
        });
        
        hbox.getChildren().add(button);
        
        // HBoxをVBoxに貼る
        vbox.getChildren().add(hbox);
        
        // ブラウザ
        engine = new WebEngine();
        view = new WebView(engine);
        // サイズの指定
        view.setPrefSize(800, 400);
        // 反射のエフェクト
        view.setEffect(new Reflection());
        
        // WebViewをVBoxに貼る
        vbox.getChildren().add(view);
        
        // VBox をルートに貼る
        root.getChildren().add(vbox);

        primaryStage.setScene(scene);
        primaryStage.setVisible(true);
        
        initLoadTask();
    }
    
    private void initLoadTask() {
        // Webページのロードのタスク
        Task task = engine.getLoadTask();

        view.setTranslateX(800);
        
        // Webページのロード開始時のイベント処理を登録
        task.setOnStarted(new EventHandler<TaskEvent>() {
            public void handle(TaskEvent event) {
                // 現在のページを左方向に800移動させる
                TranslateTransition transition 
                        = new TranslateTransition(new Duration(500));
                transition.setNode(view);
                transition.setFromX(0);
                transition.setToX(-800);
                transition.setInterpolator(Interpolator.EASE_BOTH);
                transition.play();
            }
        });

        // Webページのロード完了時のイベント処理を登録
        task.setOnDone(new EventHandler<TaskEvent>() {
            public void handle(TaskEvent event) {
                // 読み込んだページを右から800移動させる
                TranslateTransition transition 
                        = new TranslateTransition(new Duration(500));
                transition.setNode(view);
                transition.setFromX(800);
                transition.setToX(0);
                transition.setInterpolator(Interpolator.EASE_BOTH);
                transition.play();
                
                // WebEngineからURLを取得し
                //テキストボックスに反映させる
                String url = engine.getLocation();
                if (url != null && !url.isEmpty()) {
                    box.setText(url);
                }
            }
        });
    }
}
