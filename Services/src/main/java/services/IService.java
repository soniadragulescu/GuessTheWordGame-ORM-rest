package services;

import model.User;
import model.Word;

public interface IService {
    User login(IObserver client, String username, String password);
    void logout(IObserver client, String username);
    void updateUser(User user);
    void updateWord(Word word, String username);
    void saveWord(String word, String username);
    void startGame();
    //Iterable<User> getAll();
    Iterable<Word> getAllExceptUser(String username);
}
