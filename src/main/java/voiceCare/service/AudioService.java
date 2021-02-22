package voiceCare.service;

import org.apache.ibatis.annotations.Param;
import voiceCare.model.entity.Audio;

public interface AudioService {

    String audio2word(String audio_file_path) throws InterruptedException;

    String word2word(String audio_word_result);

    String word2Audio(String turing_word_result);

    void fileDir(String name);
}
