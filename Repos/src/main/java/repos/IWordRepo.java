package repos;

import model.Word;

public interface IWordRepo {
    Iterable<Word> getAllExceptUser(String username);
    void update(Word word);
    void save(Word word);
}
