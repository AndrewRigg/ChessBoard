 
import com.gtranslate.Audio;
import com.gtranslate.Language;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;
 
/**
 *
 * @web http://java-buddy.blogspot.com/
 */
public class TTS extends Application {
     
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
             
            @Override
            public void handle(ActionEvent event) {
                InputStream sound = null;
                try {
                    System.out.println("Hello World!");
                    Audio audio = Audio.getInstance();
                    sound = audio.getAudio("Hello World", Language.ENGLISH);
                    audio.play(sound);
                } catch (IOException | JavaLayerException ex) {
                    Logger.getLogger(TTS.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        sound.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TTS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
         
        StackPane root = new StackPane();
        root.getChildren().add(btn);
         
        Scene scene = new Scene(root, 300, 250);
         
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
     
}