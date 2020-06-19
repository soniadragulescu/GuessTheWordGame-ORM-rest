package repos;

import model.Word;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class WordRepo implements IWordRepo{
    static SessionFactory sessionFactory;
    private JdbcUtils jdbcUtils=new JdbcUtils();

    public WordRepo() {
        System.out.println("Initializing WordRepo... ");
        sessionFactory=jdbcUtils.getSessionFactory();
    }
    
    @Override
    public Iterable<Word> getAllExceptUser(String username) {
        try(Session session=sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx=session.beginTransaction();
                List<Word> words =
                        session.createQuery("from Word as w where w.username!=? and w.finished=false", Word.class).setParameter(0, username)
                                .setFirstResult(0).setMaxResults(20).list();
                tx.commit();
                return words;
            }catch (Exception e){
                if (tx != null)
                    tx.rollback();
                System.out.println("No words found that don't belong to the user "+ username);
                e.printStackTrace();
                return null;
            }
        }
    }

    public Iterable<Word> getAll() {
        try(Session session=sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx=session.beginTransaction();
                List<Word> words =
                        session.createQuery("from Word", Word.class)
                                .setFirstResult(0).setMaxResults(20).list();
                tx.commit();
                return words;
            }catch (Exception e){
                if (tx != null)
                    tx.rollback();
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void update(Word word) {
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                Word oldWord =
                        (Word) session.load( Word.class, word.getId());
                oldWord.setCodedWord(word.getCodedWord());
                oldWord.setFinished(word.getFinished());
                System.err.println("We've updated word "+word.getId().toString());
                tx.commit();

            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
            }
        }
    }

    @Override
    public void save(Word word) {
        try(Session session=sessionFactory.openSession()){
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(word);
                tx.commit();
                System.err.println("Am adaugat cuvantul "+word.getActualWord());
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
    }
}
