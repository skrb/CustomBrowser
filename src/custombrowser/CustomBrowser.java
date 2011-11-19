package custombrowser;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CustomBrowser extends Application {

    private TextField field;
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

        // ボタンクリックもしくはテキストフィールドでエンターを入力した場合のイベント処理
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // テキストボックスから取得した文字列を
                // WebEngine でロードする
                String url = field.getText();
                engine.load(url);
            }
        };

        // テキスト入力
        field = new TextField();
        field.setOnAction(handler);
        field.setPrefColumnCount(40);
        hbox.getChildren().add(field);

        // ボタン
        Button button = new Button("Load");
        button.setOnAction(handler);
        hbox.getChildren().add(button);

        // HBoxをVBoxに貼る
        vbox.getChildren().add(hbox);

        // ブラウザ
        view = new WebView();
        engine = view.getEngine();

        // サイズの指定
        view.setPrefSize(800, 400);
        // 反射のエフェクト
        view.setEffect(new Reflection());

        // WebViewをVBoxに貼る
        vbox.getChildren().add(view);

        // VBox をルートに貼る
        root.getChildren().add(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();

        initLoadTask();
    }

    private void initLoadTask() {
        // Webページのロードのタスク
        Worker worker = engine.getLoadWorker();

        view.setTranslateX(800);

        worker.stateProperty().addListener(new ChangeListener<Worker.State>() {

            public void changed(ObservableValue observableValue, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.RUNNING) {
                    // Webページのロード開始時の処理
                    // 現在のページを左方向に800移動させる
                    TranslateTransition transition = new TranslateTransition(new Duration(500));
                    transition.setNode(view);
                    transition.setFromX(0);
                    transition.setToX(-800);
                    transition.setInterpolator(Interpolator.EASE_BOTH);
                    transition.play();
                } else if (newState == Worker.State.SUCCEEDED) {
                    // Webページのロード完了時の処理
                    // 読み込んだページを右から800移動させる
                    TranslateTransition transition = new TranslateTransition(new Duration(500));
                    transition.setNode(view);
                    transition.setFromX(800);
                    transition.setToX(0);
                    transition.setInterpolator(Interpolator.EASE_BOTH);
                    transition.play();

                    // WebEngineからURLを取得し
                    //テキストボックスに反映させる 
                    String url = engine.getLocation();
                    if (url != null && !url.isEmpty()) {
                        field.setText(url);
                    }
                }
            }
        });

    }
}
