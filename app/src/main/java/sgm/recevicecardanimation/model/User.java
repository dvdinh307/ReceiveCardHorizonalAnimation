package sgm.recevicecardanimation.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Tuấn Sơn on 31/7/2017.
 */

public class User implements Parcelable {
    int id;
    String email;
    int remainPremium;
    boolean isPremiumUser;
    String userId;
    int status = 0;
    int isSocialType;
    int role_user;
    String introduction_video_url;
    String fullname;
    String profile;
    String avatar;
    String youtubeLink;
    String googleLink;
    String facebookLink;
    String twitterLink;
    String instagramLink;
    String created_at;
    String updated_at;
    int user_client_id;
    boolean isNewRegister;
    int point;

    public User() {
    }

    private static User instance = new User();

    protected User(Parcel in) {
        id = in.readInt();
        email = in.readString();
        remainPremium = in.readInt();
        isPremiumUser = in.readByte() != 0;
        userId = in.readString();
        status = in.readInt();
        isSocialType = in.readInt();
        role_user = in.readInt();
        introduction_video_url = in.readString();
        fullname = in.readString();
        profile = in.readString();
        avatar = in.readString();
        youtubeLink = in.readString();
        googleLink = in.readString();
        facebookLink = in.readString();
        twitterLink = in.readString();
        instagramLink = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
        user_client_id = in.readInt();
        point = in.readInt();
        isNewRegister = in.readByte() != 0;
        currentUser = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static User getInstance() {
        return instance;
    }

    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPremiumUser() {
        return isPremiumUser;
    }

    public void setPremiumUser(boolean premiumUser) {
        isPremiumUser = premiumUser;
    }

    public int getRemainPremium() {
        return remainPremium;
    }

    public void setRemainPremium(int remainPremium) {
        this.remainPremium = remainPremium;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsSocialType() {
        return isSocialType;
    }

    public void setIsSocialType(int isSocialType) {
        this.isSocialType = isSocialType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRoleUser() {
        return role_user;
    }

    public void setRoleUser(int role_user) {
        this.role_user = role_user;
    }

    public String getIntroductionVideoUrl() {
        return introduction_video_url;
    }

    public void setIntroductionVideoUrl(String introduction_video_url) {
        this.introduction_video_url = introduction_video_url;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public String getGoogleLink() {
        return googleLink;
    }

    public void setGoogleLink(String googleLink) {
        this.googleLink = googleLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getUserClientId() {
        return user_client_id;
    }

    public void setUserClientId(int user_client_id) {
        this.user_client_id = user_client_id;
    }

    public boolean isNewRegister() {
        return isNewRegister;
    }

    public void setNewRegister(boolean newRegister) {
        isNewRegister = newRegister;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public static User parse(JSONObject item) {
        User user = new User();
        user.setIsSocialType(0);
        user.setPremiumUser(false);
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeInt(remainPremium);
        dest.writeByte((byte) (isPremiumUser ? 1 : 0));
        dest.writeString(userId);
        dest.writeInt(status);
        dest.writeInt(isSocialType);
        dest.writeInt(role_user);
        dest.writeString(introduction_video_url);
        dest.writeString(fullname);
        dest.writeString(profile);
        dest.writeString(avatar);
        dest.writeString(youtubeLink);
        dest.writeString(googleLink);
        dest.writeString(facebookLink);
        dest.writeString(twitterLink);
        dest.writeString(instagramLink);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeInt(user_client_id);
        dest.writeByte((byte) (isNewRegister ? 1 : 0));
        dest.writeParcelable(currentUser, flags);
        dest.writeInt(point);
    }
}
