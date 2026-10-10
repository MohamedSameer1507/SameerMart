package com.sameer.sameermart.servlet;

import com.sameer.sameermart.config.DatabaseConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

import static org.mockito.Mockito.*;

public class ProductImageServletTest {

    private ProductImageServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;
    private ServletConfig servletConfig;
    private ByteArrayOutputStream outputBytes;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new ProductImageServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        servletContext = mock(ServletContext.class);
        servletConfig = mock(ServletConfig.class);

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        servlet.init(servletConfig);

        outputBytes = new ByteArrayOutputStream();
        ServletOutputStream sos = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) {
                outputBytes.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) {
                outputBytes.write(b, off, len);
            }
        };

        when(response.getOutputStream()).thenReturn(sos);
    }

    @Test
    public void testDoGetNullPathInfoReturnsBadRequest() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file name");
    }

    @Test
    public void testDoGetPathTraversalReturnsBadRequest() throws Exception {
        when(request.getPathInfo()).thenReturn("/..");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file name");
    }

    @Test
    public void testDoGetTraversalDotsInSegmentReturnsBadRequest() throws Exception {
        when(request.getPathInfo()).thenReturn("/uploads/test..png");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file name");
    }

    @Test
    public void testDoGetInvalidCharactersReturnsBadRequest() throws Exception {
        when(request.getPathInfo()).thenReturn("/<script>alert(1).jpg");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file name");
    }

    @Test
    public void testDoGetFallbackToDefaultProductImage() throws Exception {
        when(request.getPathInfo()).thenReturn("/sample-product.jpg");
        when(servletContext.getResourceAsStream("/images/products/sample-product.jpg")).thenReturn(null);

        byte[] defaultBytes = new byte[]{10, 20, 30};
        when(servletContext.getResourceAsStream("/images/default-product.svg"))
                .thenReturn(new ByteArrayInputStream(defaultBytes));

        servlet.doGet(request, response);

        verify(response).setContentType("image/jpeg");
        verify(response).flushBuffer();
    }

    @Test
    public void testDoGetNotFoundWhenFallbackMissing() throws Exception {
        when(request.getPathInfo()).thenReturn("/missing-product.jpg");
        when(servletContext.getResourceAsStream("/images/products/missing-product.jpg")).thenReturn(null);
        when(servletContext.getResourceAsStream("/images/default-product.svg")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found");
    }

    @Test
    public void testDoGetServesFromExternalUploadDir() throws Exception {
        File uploadDir = new File(DatabaseConfig.getUploadDir());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File tempImage = new File(uploadDir, "unit-test-image.png");
        Files.write(tempImage.toPath(), new byte[]{1, 2, 3, 4});

        try {
            when(request.getPathInfo()).thenReturn("/unit-test-image.png");

            servlet.doGet(request, response);

            verify(response).setContentType("image/png");
            verify(response).flushBuffer();
        } finally {
            Files.deleteIfExists(tempImage.toPath());
        }
    }
}
