package top.ticho.trace.spring.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.lang.NonNull;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-03-27 12:34
 */
@Slf4j
public class ResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream outputStream;
    private final ServletOutputStream servletOutputStream;
    private final PrintWriter printWriter;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        outputStream = new ByteArrayOutputStream(2048);
        servletOutputStream = new WrapperOutputStream(outputStream, response);
        printWriter = new WrapperWriter(outputStream, response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() {
        return printWriter;
    }

    public String getBody() {
        try {
            return outputStream.toString(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    static class WrapperWriter extends PrintWriter {

        private final HttpServletResponse response;
        private final ByteArrayOutputStream output;

        public WrapperWriter(ByteArrayOutputStream out, HttpServletResponse response) {
            super(out);
            this.response = response;
            this.output = out;
        }

        @Override
        public void write(int b) {
            super.write(b);
            try {
                response.getWriter().write(b);
            } catch (IOException e) {
                e.printStackTrace();
                this.setError();
            }
        }

        @Override
        public void write(@NonNull String s, int off, int len) {
            super.write(s, off, len);
            try {
                response.getWriter().write(s, off, len);
            } catch (IOException e) {
                e.printStackTrace();
                this.setError();
            }
        }
    }

    static class WrapperOutputStream extends ServletOutputStream {

        private final OutputStream outputStream;
        private final HttpServletResponse response;

        public WrapperOutputStream(OutputStream outputStream, HttpServletResponse response) {
            super();
            this.response = response;
            this.outputStream = outputStream;
        }

        @Override
        public boolean isReady() {
            if (response == null) {
                return false;
            }
            try {
                return response.getOutputStream().isReady();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            if (response != null) {
                try {
                    response.getOutputStream().setWriteListener(listener);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void write(int b) throws IOException {
            if (response != null) {
                response.getOutputStream().write(b);
            }
            outputStream.write(b);
        }

    }
}
