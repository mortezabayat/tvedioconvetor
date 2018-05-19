package ir.bayat.morteza.tvideoconvertor;

 public  class VideoObject {

    public VideoEditedInfo videoEditedInfo;

    public String attachPath;

    VideoObject(){
        videoEditedInfo = new VideoEditedInfo();

        videoEditedInfo.originalHeight = 344;
        videoEditedInfo.originalWidth = 600;
    }

    public int getId() {
        return 0;
    }
}
