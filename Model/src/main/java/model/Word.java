package model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Word")
public class Word implements Serializable {
    private Integer id;
    private String actualWord;
    private String codedWord;
    private String username;
    private boolean finished;

    public Word() {
    }

    public Word(String actualWord, String username) {
        this.actualWord = actualWord;
        String codedWord="";
        for(int i=0; i<actualWord.length(); i++){
            char c=actualWord.charAt(i);
            if(c=='A' || c=='E' || c=='I'||c=='O'||c=='U')
                codedWord+="V";
            else
                codedWord+="C";
        }
        this.codedWord = codedWord;
        this.username = username;
        this.finished=false;
    }

    public Word(Integer id, String actualWord, String codedWord, String username, boolean finished) {
        this.id = id;
        this.actualWord = actualWord;
        this.codedWord = codedWord;
        this.username = username;
        this.finished = finished;
    }

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "ActualWord")
    public String getActualWord() {
        return actualWord;
    }

    public void setActualWord(String actualWord) {
        this.actualWord = actualWord;
    }

    @Column(name="CodedWord")
    public String getCodedWord() {
        return codedWord;
    }

    public void setCodedWord(String codedWord) {
        this.codedWord = codedWord;
    }

    @Column(name="Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name="Finished")
    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
