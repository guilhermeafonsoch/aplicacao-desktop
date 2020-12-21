package db;

public class DbIntegrityExeption extends RuntimeException{

    public DbIntegrityExeption(String msg){
        super(msg);
    }

}
