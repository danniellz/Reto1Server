package signupsigninserver.dao;

import signable.Signable;
import signupsigninserver.pool.PoolConnection;

/**
 *
 * @author Daniel Brizuela
 */
public class DaoFactory {
    public Signable getDao(){
        return new DaoImplement();
    } 
}
