package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import shared.Request;
import shared.Pokemon;

/**
 * This class represents the server application, which is a Pokémon Bank.
 * It is a shared account: everyone's Pokémons are stored together.
 * @author strift
 *
 */
public class PokemonBank {
	
	public static final int SERVER_PORT = 3000;
	public static final String DB_FILE_NAME = "pokemons.db";
	
	/**
	 * The database instance
	 */
	private Database db;
	
	/**
	 * The ServerSocket instance
	 */
	private ServerSocket server;
	
	/**
	 * The Pokémons stored in memory
	 */
	private ArrayList<Pokemon> pokemons;

	public PokemonBank() throws IOException, ClassNotFoundException {
		/*
		 * TODO
		 * Here, you should initialize the Database and ServerSocket instances.
		 */
		 server = new ServerSocket(SERVER_PORT); //create the socket server object
		 db= new Database(DB_FILE_NAME);
		

		System.out.println("Banque Pokémon (" + DB_FILE_NAME + ") démarrée sur le port " + SERVER_PORT);
		
		// Let's load all the Pokémons stored in database
		this.pokemons = this.db.loadPokemons();
		this.printState();
	}
	
	/**
	 * The main loop logic of your application goes there.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void handleClient() throws IOException, ClassNotFoundException {
		System.out.println("Waiting to connect...");
		/*
		 * TODO
		 * Here, you should wait for a client to connect.
		 */
			Socket clientSocket = server.accept(); //creating socket and waiting for client connection
			System.out.println("Client connected... ");
		
		/*
		 * TODO
		 * You will one stream to read and one to write.
		 * Classes you can use:
		 * - ObjectInputStream
		 * - ObjectOutputStream
		 * - BankOperation
		 */
			ObjectOutputStream outputstream = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream inputStream  = new ObjectInputStream(clientSocket.getInputStream());

		// For as long as the client wants it
		boolean running = true;

		while (running) {
			/*
			 * TODO
			 * Here you should read the stream to retrieve a Request object
			 */
				Request request;
				request = (Request)inputStream.readObject();
			
			/*
			 * Note: the server will only respond with String objects.
			 */
			switch(request) {
			case LIST:
				System.out.println("Request: LIST");
				if (this.pokemons.size() == 0) {
					/*
					 * TODO
					 * There is no Pokémons, so just send a message to the client using the output stream.
					 *
					 */
						outputstream.writeObject("Sorry, there is no Pokémons ! ");
						outputstream.flush();

					
				} else {
					/*
					 * TODO
					 * Here you need to build a String containing the list of Pokémons, then write this String
					 * in the output stream.
					 * Classes you can use:
					 * - StringBuilder
					 * - String
					 * - the output stream
					 */
						pokemons=db.loadPokemons();
						StringBuilder stringBuilder = new StringBuilder();

						for(int i=0 ; i< pokemons.size(); i++){
							stringBuilder.append(pokemons.get(i));
						}
						outputstream.writeObject(stringBuilder);
						outputstream.flush();
				}
				break;
				
			case STORE:
				System.out.println("Request: STORE");
				/*
				 * TODO
				 * If the client sent a STORE request, the next object in the stream should be a Pokémon.
				 * You need to retrieve that Pokémon and add it to the ArrayList.
				 */

					Pokemon pok = (Pokemon) inputStream.readObject();
					pokemons.add((Pokemon) inputStream.readObject());
				
				
				/*
				 * TODO
				 * Then, send a message to the client so he knows his Pokémon is safe.
				 */
					outputstream.writeObject("The Pokémon is safe now ! " + pok.toString() + "has been received ");
					outputstream.flush();
				

				break;
				
			case CLOSE:
				System.out.println("Request: CLOSE");
				/*
				 * TODO
				 * Here, you should use the output stream to send a nice 'Au revoir !' message to the client. 
				 */
					outputstream.writeObject("Au revoir! ");
					outputstream.flush(); //vider le tampon
				
				// Closing the connection
				System.out.println("Fermeture de la connexion...");
				running = false;
				break;
			}
			this.printState();

		};
		
		/*
		 * TODO
		 * Now you can close both I/O streams, and the client socket.
		 */
			clientSocket.close(); //closing the socket
			outputstream.close(); //closing the outputstream
			inputStream.close();  //closing the input stream
		
		/*
		 * TODO
		 * Now that everything is done, let's update the database.
		 *
		 */
			db.savePokemons(pokemons);
		
	}
	
	/**
	 * Print the current state of the bank
	 */
	private void printState() {
		System.out.print("[");
		for (int i = 0; i < this.pokemons.size(); i++) {
			if (i > 0) {
				System.out.print(", ");
			}
			System.out.print(this.pokemons.get(i));
		}
		System.out.println("]");
	}
	
	/**
	 * Stops the server.
	 * Note: This function will never be called in this project.
	 * @throws IOException 
	 */
	public void stop() throws IOException {
		this.server.close();
	}
}
