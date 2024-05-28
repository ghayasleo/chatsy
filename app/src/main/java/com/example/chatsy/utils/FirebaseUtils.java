package com.example.chatsy.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;


public class FirebaseUtils {

    public static String currentUserID(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserID());
    }

    public static boolean isLoggedIn() {
        if (currentUserID() != null) {
            return true;
        }
        return false;
    }

    public static CollectionReference allUsersCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    //it'll give chatroomId from firebase
    public static DocumentReference getChatroomReference(String chatroomId){
        return  FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    //two users has same chatroomId...if user1 is chatting to user2 or user2 is chatting with user1
    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userId){
        if(userId.get(0).equals(FirebaseUtils.currentUserID())){
            return allUsersCollectionReference().document(userId.get(1));
        }else{
            return allUsersCollectionReference().document(userId.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        return  new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(FirebaseUtils.currentUserID());
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(otherUserId);
    }

}
