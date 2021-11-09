package signupsigninserver.dao;

import signable.Signable;

/**
 * Class that return a new DaoImplement
 * 
 * @author Daniel Brizuela
 */
public class DaoFactory {

    /**
     *
     * @return
     */
    public Signable getDao(){
        return new DaoImplement();
    } 
}
