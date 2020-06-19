package services;

import model.User;
import model.Word;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IObserver extends Remote {
    void letterGuessed(List<Word> words, List<User> users) throws RemoteException;
    void gameStarted() throws RemoteException;
    void actualGameStarted() throws RemoteException;
}
