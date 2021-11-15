package signupsigninserver.dao;

import signable.Signable;

/**
 * Class that return a new DaoImplement
 *
 * @author Daniel Brizuela
 * @version 1.0
 */
public class DaoFactory {

    /**
     * Method that return a new DaoImplement
     *
     * @return a new DaoImplement
     */
    public static Signable getDao() {
        return new DaoImplement();
    }
}
