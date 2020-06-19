package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;
import model.Word;
import services.IObserver;
import services.IService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class MainController extends UnicastRemoteObject implements IObserver, Serializable {
    private IService service;
    private User user;
    ObservableList<Word> words= FXCollections.observableArrayList();

    public MainController() throws RemoteException {
    }

    public void setUser(User user){
        this.user=user;
    }

    public void setService(IService service){
        this.service=service;
        setWords();
        init();
    }

    public void setWords(){
        List<Word> list=new ArrayList<>();
        for(Word w: service.getAllExceptUser(user.getUsername())){
            list.add(w);
        }
//        if (list.size()==0){
//            showErrorMessage("Ai ghicit toate cuvintele! Ai "+user.getScore().toString()+" puncte!");
//            service.logout(this,user.getUsername());
//            Platform.exit();
//        }
        words.setAll(list);
    }

    @FXML
    TableView<Word> tableWords;

    @FXML
    TableColumn<Word, String> columnCodedWord;

    @FXML
    TableColumn<Word, String> columnUsername;

    @FXML
    TextField textboxLetter;

    @FXML
    Button buttonGuess;

    @FXML
    Button buttonStartGame;

    @FXML
    Button buttonSendWord;

    @FXML
    TextField textboxWord;

    @FXML
    Label labelEnterWord;

    @FXML
    Label labelEnterLetter;

    public void init(){
        System.out.println(user.getUsername());
        columnCodedWord.setCellValueFactory((new PropertyValueFactory<Word, String>("codedWord")));
        columnUsername.setCellValueFactory(new PropertyValueFactory<Word,String>("username"));

        tableWords.setItems(words);

        tableWords.setVisible(false);
        labelEnterLetter.setVisible(false);
        buttonGuess.setVisible(false);
        textboxLetter.setVisible(false);

        textboxWord.setVisible(false);
        labelEnterWord.setVisible(false);
        buttonSendWord.setVisible(false);
    }

    @FXML
    public void logout(){
        this.service.logout(this,this.user.getUsername());
        Platform.exit();
    }

    @FXML
    public void guess(){
        Integer id=tableWords.getSelectionModel().getSelectedItem().getId();
        String codedWord=tableWords.getSelectionModel().getSelectedItem().getCodedWord();
        String actualWord=tableWords.getSelectionModel().getSelectedItem().getActualWord();
        boolean finished=tableWords.getSelectionModel().getSelectedItem().getFinished();
        String username=tableWords.getSelectionModel().getSelectedItem().getUsername();

        Integer letterGuessed=0;
        String userLetters="";
        String letter=textboxLetter.getText();
        if(user.getLetters().equals("0")){
            user.setLetters(letter);
        }
        else {
            userLetters=user.getLetters();
            userLetters+=letter;
            user.setLetters(userLetters);
        }

        String newCodedWord="";
        for(int i=0; i<actualWord.length(); i++){
            if(actualWord.charAt(i)==letter.charAt(0)){
                newCodedWord+=actualWord.charAt(i);
                letterGuessed+=1;
            }
            else {
                newCodedWord+=codedWord.charAt(i);
            }
        }

        if(actualWord.equals(newCodedWord)){
            finished=true;
        }

        Word word=new Word(id, actualWord, newCodedWord,username,finished);
        Integer initialScore=user.getScore();
        initialScore+=letterGuessed;
        user.setScore(initialScore);
        service.updateUser(user);
        service.updateWord(word, username);
       // setWords();

        List<Word> list=new ArrayList<>();
        for(Word w: service.getAllExceptUser(user.getUsername())){
            list.add(w);
        }
        if (list.size()==0){
            showErrorMessage("Ai ghicit toate cuvintele! Ai "+user.getScore().toString()+" puncte!");
            service.logout(this,user.getUsername());
            Platform.exit();
        }
    }

    public void startGame(){
        service.startGame();
    }

    @Override
    public void letterGuessed(List<Word> newWords, List<User> users) throws RemoteException {
        System.out.println(newWords.get(0).getActualWord()+" "+newWords.get(1).getActualWord());
        List<Word> newList=new ArrayList<>();
        for(Word w: newWords)
            if(!(w.getUsername().equals(user.getUsername())))
                newList.add(w);
        words.setAll(newList);
        users.forEach(user1 -> System.out.println(user1.getUsername()));
        for(User u: users){
            if(u.getUsername().equals(user.getUsername())) {
                user.setRound(u.getRound());
                System.out.println(u.getRound().toString());
            }
        }
        if(user.getRound().equals(1)){
            buttonGuess.setVisible(true);
        }else{
            buttonGuess.setVisible(false);
        }
        //setWords();
    }

    @Override
    public void gameStarted(){
        buttonStartGame.setVisible(false);

        buttonSendWord.setVisible(true);
        textboxWord.setVisible(true);
        labelEnterWord.setVisible(true);

    }

    @Override
    public void actualGameStarted() throws RemoteException {
        this.labelEnterWord.setVisible(false);
        this.textboxWord.setVisible(false);
        this.buttonSendWord.setVisible(false);

        this.tableWords.setVisible(true);
        this.labelEnterLetter.setVisible(true);
        this.textboxLetter.setVisible(true);
        if(user.getRound().equals(1)) {
            this.buttonGuess.setVisible(true);
        }else {
            this.buttonGuess.setVisible(false);
        }

        setWords();
    }

    private static void showErrorMessage(String err){
        Alert message = new Alert(Alert.AlertType.ERROR);
        message.setTitle("Error message!");
        message.setContentText(err);
        message.showAndWait();
    }

    public void sendWord() {
        String word=textboxWord.getText();
        service.saveWord(word, this.user.getUsername());
    }
}
