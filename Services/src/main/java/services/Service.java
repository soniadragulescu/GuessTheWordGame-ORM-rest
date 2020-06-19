package services;

import model.User;
import model.Word;
import repos.UserRepo;
import repos.WordRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements IService{
    private UserRepo userRepo;
    private WordRepo wordRepo;
    private List<IObserver> observers;
    public static boolean gameStarted;
    public static Integer participants;
    public static  Integer wordsEntered;

    public Service(UserRepo userRepo, WordRepo wordRepo) {
        this.userRepo = userRepo;
        this.wordRepo = wordRepo;
        observers=new ArrayList<>();
        this.gameStarted=false;
        this.participants=0;
        this.wordsEntered=0;
    }

    @Override
    public synchronized User login(IObserver client, String username, String password) {
        if(gameStarted==true)
            return null;
        User user=userRepo.findOne(username, password);
        if(user!=null){
            observers.add(client);
            participants+=1;
            user.setRound(participants);
            userRepo.update(user);
        }
        return user;
    }

    @Override
    public void logout(IObserver client, String username) {
        observers.remove(client);
        User user=userRepo.findOneByUsername(username);
        user.setRound(null);
        if(participants==1){
            gameStarted=false;
            participants=0;
        }
        userRepo.update(user);
        if(participants>0)
            participants-=1;
    }

    @Override
    public void updateUser(User user) {
        userRepo.update(user);
    }

    @Override
    public void updateWord(Word word, String username) {
        wordRepo.update(word);
        for (User u:userRepo.getAll()
             ) {
            if(u.getRound()!=null) {
                Integer round = u.getRound();
                if (round == participants)
                    u.setRound(1);
                else {
                    round += 1;
                    u.setRound(round);
                }
                userRepo.update(u);
            }
        }
        notifyUsers(username);
    }

    @Override
    public void saveWord(String actualWord, String username) {
        Word word=new Word(actualWord, username);
        wordRepo.save(word);
        this.wordsEntered+=1;
        if(this.wordsEntered==participants)
            startActualGame();
    }

    @Override
    public void startGame() {
        gameStarted=true;

        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer:observers) {
            executor.execute(() -> {
                try {
                    System.out.println("notifying users...");

                    observer.gameStarted();
                } catch (Exception e) {
                    System.out.println("error notifying users...");
                }
            });
        }

        executor.shutdown();

    }

    @Override
    public Iterable<Word> getAllExceptUser(String username) {
        return wordRepo.getAllExceptUser(username);
    }

    private final int defaultThreadsNo=5;
    private void notifyUsers(String username){
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer:observers) {
            executor.execute(() -> {
                try {
                    System.out.println("notifying users...");
                    List<Word> words = new ArrayList<>();
                    for (Word word : wordRepo.getAll()
                    ) {
                        words.add(word);
                    }
                    List<User> users = new ArrayList<>();
                    for (User u : userRepo.getAll()) {
                        if(u.getRound()!=null){
                            users.add(u);
                            System.out.println(u.getUsername()+" "+u.getRound().toString());
                        }
                    }
                    observer.letterGuessed(words, users);
                } catch (Exception e) {
                    System.out.println("error notifying users...");
                }
            });
        }

        executor.shutdown();
    }

    public void startActualGame(){
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer:observers) {
            executor.execute(() -> {
                try {
                    System.out.println("notifying users...");

                    observer.actualGameStarted();
                } catch (Exception e) {
                    System.out.println("error notifying users...");
                }
            });
        }

        executor.shutdown();
    }
}
