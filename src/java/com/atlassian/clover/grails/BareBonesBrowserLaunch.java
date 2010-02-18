package com.atlassian.clover.grails;/////////////////////////////////////////////////////////
//  Bare Bones Browser Launch                          //
//  Version 3.0 (February 7, 2010)                     //
//  By Dem Pilafian                                    //
//  Supports:                                          //
//     Mac OS X, GNU/Linux, Unix, Windows XP/Vista/7   //
//  Example Usage:                                     //
//     String url = "http://www.centerkey.com/";       //
//     BareBonesBrowserLaunch.openURL(url);            //
//  Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

import java.util.Arrays;

public class BareBonesBrowserLaunch
{

    static final String[] browsers = {"google-chrome", "firefox", "opera",
            "konqueror", "epiphany", "seamonkey", "galeon", "kazehakase", "mozilla"};
    static final String errMsg = "Error attempting to launch web browser";

    public static void openURL(String url)
    {
        try
        {  //attempt to use Desktop library from JDK 1.6+ (even if on 1.5)
            Class<?> d = Class.forName("java.awt.Desktop");
            d.getDeclaredMethod("browse", new Class[]{java.net.URI.class}).invoke(
                    d.getDeclaredMethod("getDesktop").invoke(null),
                    new Object[]{java.net.URI.create(url)});
            //above code mimics:
            //   java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        }
        catch (Exception ignore)
        {  //library not available or failed
            String osName = System.getProperty("os.name");
            try
            {
                if (osName.startsWith("Mac OS"))
                {
                    Class.forName("com.apple.eio.FileManager").getDeclaredMethod(
                            "openURL", new Class[]{String.class}).invoke(null,
                            new Object[]{url});
                }
                else if (osName.startsWith("Windows"))
                    Runtime.getRuntime().exec(
                            "rundll32 url.dll,FileProtocolHandler " + url);
                else
                { //assume Unix or Linux
                    boolean found = false;
                    for (String browser : browsers)
                        if (!found)
                        {
                            found = Runtime.getRuntime().exec(
                                    new String[]{"which", browser}).waitFor() == 0;
                            if (found)
                                Runtime.getRuntime().exec(new String[]{browser, url});
                        }
                    if (!found)
                        throw new Exception(Arrays.toString(browsers));
                }
            }
            catch (Exception e)
            {
                System.out.println("Could not open browser. " + e.getMessage());
            }
        }
    }

}
