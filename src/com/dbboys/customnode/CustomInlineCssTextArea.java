package com.dbboys.customnode;


import com.dbboys.app.Main;
import com.dbboys.util.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CharacterHit;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.TwoDimensional;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CustomInlineCssTextArea extends InlineCssTextArea {
    private MenuItem copyItem ;
    public ContextMenu inlineCssMenu = new ContextMenu();

    public CustomInlineCssTextArea() {
        super();
        setEditable(false);
        copyItem= MenuItemUtil.createCopyMenuItem(true);

        setContextMenu(inlineCssMenu);

        inlineCssMenu.getItems().addAll(copyItem);
        inlineCssMenu.setOnShowing((event) -> {
            if(getSelectedText().isEmpty()){
                copyItem.setDisable(true);
            }else{
                copyItem.setDisable(false);
            }
        });
        copyItem.setOnAction(event -> {
            if(!getSelectedText().isEmpty()){
                copy();
            }
        });

        setParagraphGraphicFactory(LineNumberFactory.get(this));


    }



}
