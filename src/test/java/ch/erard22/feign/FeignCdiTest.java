package ch.erard22.feign;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import feign.Param;
import feign.RequestLine;
import feign.cdi.api.FeignClient;
import feign.cdi.impl.FeignExtension;
import feign.gson.GsonDecoder;

@RunWith(CdiRunner.class)
@AdditionalClasses(FeignExtension.class)
public class FeignCdiTest {

    @FeignClient(url="https://api.github.com", decoder = GsonDecoder.class)
    interface GitHubCdi {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Example.Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    static class Contributor {
        String login;
        int contributions;
    }

    @Inject
    private GitHubCdi github;

    @Test
    public void gitHubTest() {
        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "3128");

        // Fetch and print a list of the contributors to this library.
        List<Example.Contributor> contributors = github.contributors("OpenFeign", "feign");
        for (Example.Contributor contributor : contributors) {
            System.out.println(contributor.login + " (" + contributor.contributions + ")");
        }
        assertTrue(!contributors.isEmpty());
    }
}
