# TVideocompressor
[![](https://jitpack.io/v/mortezabayat/tvedioconvetor.svg)](https://jitpack.io/#mortezabayat/tvedioconvetor)
Video compressor derived from Telegram open source project

# How to used.
            val destFile = File(Environment.getExternalStorageDirectory(),
                    File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME
                            + Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR + File.separator
                            + random.nextInt(10000) + ".mp4")

            val videoInfo = VideoInfo(tempFile.path, destFile.path, selectedCompression, mute)
            MediaController.getInstance().convertVideo(videoInfo)
