package signupsigninserver.dao;

import signable.Signable;

/**
 * Class that return a new DaoImplement
 * 
 * @author Daniel Brizuela
 */
public class DaoFactory {

    /**
     * Method that return a new DaoImplement
     * 
     * @return a new DaoImplement
     */
    public Signable getDao(){
        return new DaoImplement();
    } 
}
