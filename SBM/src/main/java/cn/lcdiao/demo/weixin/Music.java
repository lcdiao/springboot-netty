package cn.lcdiao.demo.weixin;

/**
 * Created by diao on 2019/3/22
 */
public class Music {
    private String title;
    private String description;
    private String musicURL;
    private String hQMusicUrl;
    private String thumbMediaId;

    public Music(String title, String description, String musicURL, String hQMusicUrl, String thumbMediaId) {
        this.title = title;
        this.description = description;
        this.musicURL = musicURL;
        this.hQMusicUrl = hQMusicUrl;
        this.thumbMediaId = thumbMediaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMusicURL() {
        return musicURL;
    }

    public void setMusicURL(String musicURL) {
        this.musicURL = musicURL;
    }

    public String gethQMusicUrl() {
        return hQMusicUrl;
    }

    public void sethQMusicUrl(String hQMusicUrl) {
        this.hQMusicUrl = hQMusicUrl;
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
    }

    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", musicURL='" + musicURL + '\'' +
                ", hQMusicUrl='" + hQMusicUrl + '\'' +
                ", thumbMediaId='" + thumbMediaId + '\'' +
                '}';
    }
}
