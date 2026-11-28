package com.dyno.deaddrop.util;

import org.springframework.stereotype.Component;

import com.dyno.deaddrop.dto.DeadDropUploadResponse;

@Component
public class DeadDropHtmlResponseBuilder {

    public String buildResultPage(DeadDropUploadResponse response) {

        String downloadUrl = escapeHtml(response.downloadUrl());
        String deletionSecret = escapeHtml(response.deletionSecret());

        return """
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="robots" content="noindex, nofollow">
                    <title>Dead Drop Created</title>
                    <link rel="stylesheet" href="/dead-drop/result.css">
                </head>

                <body>

                    <main class="container">

                        <h1>Dead Drop Created</h1>

                        <p>Your encrypted file was uploaded successfully.<br>
                        Copy and paste the url directly into the browser will download the file or use Download feature from the previous page. 
                        </p>

                        <section>
                            <label for="download-url">Public download URL</label>

                            <input
                                id="download-url"
                                type="text"
                                value="%s"
                                readonly
                            >

                            <p>Select and copy this URL.</p>
                        </section>
        		<!--
                        <section>
                            <label for="deletion-secret">Deletion secret</label>

                            <input
                                id="deletion-secret"
                                type="text"
                                value="%s"
                                readonly
                            >

                            <p>Keep this secret safe. You will need it to delete your Dead Drop.</p>
                        </section>
        		-->
                        <p>Expiration: %s</p>

                        <a href="/dead-drop/index.html">Create another Dead Drop</a>

                    </main>

                </body>
                </html>
                """.formatted(
                    downloadUrl,
                    deletionSecret,
                    response.expiresAt()
                );
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}