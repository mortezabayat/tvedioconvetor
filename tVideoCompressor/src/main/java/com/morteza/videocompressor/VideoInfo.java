package com.morteza.videocompressor;

public class VideoInfo {

    final String sourcePath;
    final String destPath;
    final int selectedCompression;
    final boolean mute;

    public VideoInfo(String sourcePath, String destPath, int selectedCompression, boolean mute) {
        this.sourcePath = sourcePath;
        this.destPath = destPath;
        this.selectedCompression = selectedCompression;
        this.mute = mute;
    }


    public String getSourcePath() {
        return sourcePath;
    }

    public String getDestPath() {
        return destPath;
    }

    public int getSelectedCompression() {
        return selectedCompression;
    }

    public boolean isMute() {
        return mute;
    }

    public static class Builder {

        String sourcePath;
        String destPath;
        int selectedCompression;
        boolean mute;

        public Builder setSourcePath(String srcPath) {
            this.sourcePath = srcPath;
            return this;
        }

        public Builder setDestPath(String destPath) {
            this.destPath = destPath;
            return this;
        }

        public Builder setSelectedCompression(int selectedCompression) {
            this.selectedCompression = selectedCompression;
            return this;
        }

        public Builder setMute(boolean mute) {
            this.mute = mute;
            return this;
        }

        public VideoInfo build() {

            return new VideoInfo(sourcePath, destPath, selectedCompression, mute);
        }
    }
}
