package com.assistantmodal.aiassistant.gui;

import com.assistantmodal.aiassistant.util.ContextUtil;
import com.sun.tools.javac.Main;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.MimeTypeUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplicationController implements Initializable {

    private final Logger log=Logger.getLogger(MainApplicationController.class.getName());

    @FXML
    public TextArea textAreaAiResponse;
    @FXML
    public TextArea textAreaInput;
    @FXML
    public ProgressBar progresBar;
    @FXML
    public ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupGui();
    }

    private void setupGui() {
    }

    public void handleButtonSendAction(ActionEvent actionEvent) {
        var textPrompt=textAreaInput.getText();
        if(textPrompt==null || textPrompt.isEmpty()){
                return;
        }
        imageView.setVisible(false);
        progresBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        textAreaAiResponse.setText("");
        if(imageView.getImage()==null){
           processTextOnlyPrompt(textPrompt);
        }
        else{
            processTextWithImagePrompt(textPrompt,imageView.getImage());
        }

    }

    private void processTextWithImagePrompt(String textPrompt, Image image) {
        log.log(Level.INFO,"Processing LLM input with image");
        var chatClient=ContextUtil.getApplicationContext().getBean(ChatClient.class);
        var byteArrayOutputStream=convertJavaFxImageIntoPng(image);
        var llmResponse= chatClient.prompt()
                .user(promptUserSpec -> {
                    promptUserSpec.text(textPrompt)
                            .media(MimeTypeUtils.IMAGE_PNG,new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
                })
                .stream()
                .content()
                .doOnComplete(()-> Platform.runLater(()-> progresBar.setProgress(0)))
                .subscribe(token ->{
                    Platform.runLater(()-> textAreaAiResponse.appendText(token));
                });
    }

    private ByteArrayOutputStream convertJavaFxImageIntoPng(Image image)  {
        try {
            var bufferedImage=  SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream bao=new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",bao);
            return bao;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processTextOnlyPrompt(String textPrompt) {
        log.log(Level.INFO,"Processing LLM input with text");
        var chatClient=ContextUtil.getApplicationContext().getBean(ChatClient.class);
        var llmResponse= chatClient.prompt()
                .user(textPrompt)
                .stream()
                .content()
                .doOnComplete(()-> Platform.runLater(()-> progresBar.setProgress(0)))
                .subscribe(token ->{
                    Platform.runLater(()-> textAreaAiResponse.appendText(token));
                });
    }

    @FXML
    public void handleButtonImagePickerAction(ActionEvent actionEvent) {
        var clipboard=Clipboard.getSystemClipboard();
        if(clipboard==null){
            log.log(Level.INFO,"Clipboard is null");
            return;
        }
       if(!clipboard.hasImage()){
           log.log(Level.INFO,"Clipboard has no image");
           return;
       }
       var image=clipboard.getImage();
       imageView.setImage(image);
        log.log(Level.INFO,"Image loaded Successfully");
        imageView.setVisible(true);
    }
}
