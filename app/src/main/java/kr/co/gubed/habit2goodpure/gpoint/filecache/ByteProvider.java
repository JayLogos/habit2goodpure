package kr.co.gubed.habit2goodpure.gpoint.filecache;

import java.io.IOException;
import java.io.OutputStream;

public interface ByteProvider {

	void writeTo(OutputStream os) throws IOException;

}