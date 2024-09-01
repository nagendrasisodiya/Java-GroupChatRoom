package org.Library;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    String userName;
    public Client(Socket socket, String userName){
        try{
            this.socket = socket;
            this.userName = userName;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            closeEveryThing(socket, bufferedReader, bufferedWriter);
        }
    }
    public  void sendMessage(){
        try{
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();;
            Scanner scanner=new Scanner(System.in);
            while (socket.isConnected()){
                String messageToSend=scanner.nextLine();
                bufferedWriter.write(userName+": "+messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEveryThing(socket, bufferedReader, bufferedWriter);
        }
    }
    public void listenMessage(){
        new Thread(new Runnable() {
            public void run() {
                String message;
                while(socket.isConnected()){
                   try{
                       message=bufferedReader.readLine();
                       System.out.println(message);
                   }catch (IOException e){
                       closeEveryThing(socket, bufferedReader, bufferedWriter);
                   }
                }
            }
        }).start();
    }
    public void closeEveryThing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String userName = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, userName);
        client.listenMessage();
        client.sendMessage();
        ;
    }
}
