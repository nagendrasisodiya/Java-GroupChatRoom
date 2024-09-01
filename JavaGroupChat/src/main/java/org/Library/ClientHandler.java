package org.Library;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

//each object of this class is responsible for communicating with client and not only this
//this class implements interface called runnable
public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;
    public ClientHandler(Socket socket) {
        try{
            this.socket=socket;
            bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName=bufferedReader.readLine();
            clientHandlers.add(this);
            broadCastMessage("SERVER:"+userName+" has entered the chat!");
        }catch(IOException e){
            closeEverthing( socket, bufferedWriter, bufferedReader);
        }
    }

    private void broadCastMessage(String s) {
        for(ClientHandler clientHandler:clientHandlers){
            try{
                if(!clientHandler.userName.equals(userName)){
                    clientHandler.bufferedWriter.write(s);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch(IOException e){
                closeEverthing( socket, bufferedWriter, bufferedReader);
            }
        }
    }
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadCastMessage("SERVER:"+userName+" has left the chat!");
    }

    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()){
            try{
                messageFromClient=bufferedReader.readLine();
                broadCastMessage(messageFromClient);
            }catch(IOException e){
                closeEverthing( socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    private void closeEverthing(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClientHandler();
        try{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
