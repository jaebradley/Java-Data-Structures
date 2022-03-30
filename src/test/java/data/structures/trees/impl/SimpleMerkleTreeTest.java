package data.structures.trees.impl;

import junit.framework.TestCase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertNotEquals;

public class SimpleMerkleTreeTest extends TestCase {

    public void test() {
        final MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("unexpected", e);
        }

        final MessageDigest sha1;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("uenxpected", e);
        }

        final SimpleMerkleTree md5Tree = new SimpleMerkleTree(
                md5::digest,
                new byte[][]{
                        "foo".getBytes(StandardCharsets.US_ASCII),
                        "bar".getBytes(StandardCharsets.US_ASCII)
                }
        );

        final SimpleMerkleTree sha1Tree = new SimpleMerkleTree(
                sha1::digest,
                new byte[][]{
                        "foo".getBytes(StandardCharsets.US_ASCII),
                        "bar".getBytes(StandardCharsets.US_ASCII)
                }
        );

        final SimpleMerkleTree md5Tree2 = new SimpleMerkleTree(
                md5::digest,
                new byte[][]{
                        "foo".getBytes(StandardCharsets.US_ASCII),
                        "bar".getBytes(StandardCharsets.US_ASCII)
                }
        );

        final SimpleMerkleTree md5Tree3 = new SimpleMerkleTree(
                md5::digest,
                new byte[][]{
                        "bar".getBytes(StandardCharsets.US_ASCII),
                        "foo".getBytes(StandardCharsets.US_ASCII)
                }
        );

        assertNotEquals(md5Tree, sha1Tree);
        assertNotEquals(sha1Tree, md5Tree);
        assertEquals(md5Tree, md5Tree);
        assertEquals(sha1Tree, sha1Tree);
        assertEquals(md5Tree, md5Tree2);
        assertEquals(md5Tree2, md5Tree);
        assertNotEquals(md5Tree, md5Tree3);
        assertNotEquals(md5Tree3, md5Tree);
    }
}