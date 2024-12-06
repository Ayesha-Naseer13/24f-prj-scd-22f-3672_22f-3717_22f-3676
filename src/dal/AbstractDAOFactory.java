package dal;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class AbstractDAOFactory implements IDAOFactory {
    private static IDAOFactory instance = null;

    public static IDAOFactory getInstance() {
        if (instance == null) {
            String factoryClassName = null;
            try (FileInputStream input = new FileInputStream("CSS/config.properties")) {

                Properties prop = new Properties();
                prop.load(input); 
                factoryClassName = prop.getProperty("dal.factory"); 

                if (factoryClassName == null) {
                    throw new IllegalArgumentException("Factory class name not specified in config.properties");
                }

                Class<?> clazz = Class.forName(factoryClassName); 
                instance = (IDAOFactory) clazz.getDeclaredConstructor().newInstance(); 
            } catch (IOException e) {
                System.err.println("Error loading config.properties file: " + e.getMessage());
                e.printStackTrace(); 
            } catch (ClassNotFoundException e) {
                System.err.println("Factory class not found: " + factoryClassName);
                e.printStackTrace(); 
            } catch (ReflectiveOperationException e) {
                System.err.println("Error instantiating the factory class: " + e.getMessage());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                System.err.println("Illegal argument: " + e.getMessage());
                e.printStackTrace(); 
            }
        }
        return instance; 
    }

	
}
