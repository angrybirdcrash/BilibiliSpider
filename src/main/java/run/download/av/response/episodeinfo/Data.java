/**
  * Copyright 2019 bejson.com 
  */
package run.download.av.response.episodeinfo;
import java.util.List;

/**
 * Auto-generated: 2019-04-26 19:59:12
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Data {

    private String from;
    private String result;
    private String message;
    private int quality;
    private String format;
    private int timelength;
    private String accept_format;
    private List<String> accept_description;
    private List<Integer> accept_quality;
    private int video_codecid;
    private String seek_param;
    private String seek_type;
    private List<Durl> durl;
    public void setFrom(String from) {
         this.from = from;
     }
     public String getFrom() {
         return from;
     }

    public void setResult(String result) {
         this.result = result;
     }
     public String getResult() {
         return result;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setQuality(int quality) {
         this.quality = quality;
     }
     public int getQuality() {
         return quality;
     }

    public void setFormat(String format) {
         this.format = format;
     }
     public String getFormat() {
         return format;
     }

    public void setTimelength(int timelength) {
         this.timelength = timelength;
     }
     public int getTimelength() {
         return timelength;
     }

    public void setAccept_format(String accept_format) {
         this.accept_format = accept_format;
     }
     public String getAccept_format() {
         return accept_format;
     }

    public void setAccept_description(List<String> accept_description) {
         this.accept_description = accept_description;
     }
     public List<String> getAccept_description() {
         return accept_description;
     }

    public void setAccept_quality(List<Integer> accept_quality) {
         this.accept_quality = accept_quality;
     }
     public List<Integer> getAccept_quality() {
         return accept_quality;
     }

    public void setVideo_codecid(int video_codecid) {
         this.video_codecid = video_codecid;
     }
     public int getVideo_codecid() {
         return video_codecid;
     }

    public void setSeek_param(String seek_param) {
         this.seek_param = seek_param;
     }
     public String getSeek_param() {
         return seek_param;
     }

    public void setSeek_type(String seek_type) {
         this.seek_type = seek_type;
     }
     public String getSeek_type() {
         return seek_type;
     }

    public void setDurl(List<Durl> durl) {
         this.durl = durl;
     }
     public List<Durl> getDurl() {
         return durl;
     }

}