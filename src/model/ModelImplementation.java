/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import datatransferobject.MessageEnum;
import datatransferobject.Model;
import datatransferobject.Package;
import datatransferobject.User;
import exceptions.ConnectionErrorException;
import exceptions.InvalidUserException;
import exceptions.MaxConnectionExceededException;
import exceptions.TimeOutException;
import exceptions.UserExistException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The implementation of the model
 * @author Julen y Mikel
 */
public class ModelImplementation implements Model {
    private final ResourceBundle bundle = ResourceBundle.getBundle("resources.config");
    private final int PORT = Integer.parseInt(bundle.getString("PORT"));
    private final String HOST = bundle.getString("HOST");
    private Socket sckt;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private static final Logger LOGGER = Logger.getLogger("ModelImplementation");
    /**
     * Method that takes a user from the view and sends a package to the server
     * @param user Class that has all data from a user
     * @return p Class that contains a user and a MessageEnum
     */

    @Override
    public User doSignIn(User user) throws InvalidUserException, MaxConnectionExceededException, ConnectionErrorException, TimeOutException {
        Package pack;
        try {
            sckt = new Socket(HOST,PORT);
            oos = new ObjectOutputStream(sckt.getOutputStream());
            
            pack = new Package(user,MessageEnum.RE_SIGNIN);
            oos.writeObject(pack);
            
            ois = new ObjectInputStream(sckt.getInputStream());
            pack = (Package) ois.readObject();
            
            switch(pack.getMessage()) {
                case AN_INVALIDUSER:
                    throw new InvalidUserException("Incorrect username or password.");
                case AN_MAXCONNECTION:
                    throw new MaxConnectionExceededException("The maximum number of request reached. Try again later.");
                case AN_CONNECTIONERROR:
                    throw new ConnectionErrorException("Connection error with the database. Try again later.");
            }
        } catch (IOException | ClassNotFoundException ex) {
            String msg = "Connection error with the server. Try again later.";
            LOGGER.log(Level.SEVERE,msg);
            throw new TimeOutException(msg);
        } finally {
            try {
                if(oos != null){
                    oos.close();
                }if(ois != null){
                   ois.close(); 
                }if(sckt != null){
                    sckt.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ModelImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pack.getUser();
    }
    /**
     * Method that takes a user from the view and sends a package to the server
     * @param user Class that has all data from a user
     */

    @Override
    public void doSignUp(User user) throws MaxConnectionExceededException, ConnectionErrorException, UserExistException, TimeOutException {
        try {
            sckt = new Socket(HOST,PORT);
            oos = new ObjectOutputStream(sckt.getOutputStream());
            
            Package pack = new Package(user,MessageEnum.RE_SIGNUP);
            oos.writeObject(pack);
            
            ois = new ObjectInputStream(sckt.getInputStream());
            pack = (Package) ois.readObject();
            switch(pack.getMessage()) {
                case AN_USEREXIST:
                    throw new UserExistException("This user already exist.");
                case AN_MAXCONNECTION:
                    throw new MaxConnectionExceededException("The maximum number of request reached. Try again later.");
                case AN_CONNECTIONERROR:
                    throw new ConnectionErrorException("Connection error with the database. Try again later.");
            }
        } catch (IOException | ClassNotFoundException ex) {
            String msg = "Connection error with the server. Try again later.";
            LOGGER.log(Level.SEVERE,msg);
            throw new TimeOutException(msg);
        } finally {
            try {
                if(oos != null){
                    oos.close();
                }if(ois != null){
                   ois.close(); 
                }if(sckt != null){
                    sckt.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ModelImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
