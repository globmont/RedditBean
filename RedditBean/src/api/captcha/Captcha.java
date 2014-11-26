package api.captcha;

import java.awt.image.BufferedImage;

import api.driver.Driver;

public class Captcha {
	private static String iden;
	private static BufferedImage image;
	
	public Captcha(String iden, BufferedImage image) {
		this.iden = iden;
		this.image = image;
	}
	
	public static Captcha getNewCaptcha() {
		return Driver.getCaptcha();
	}
	
	public String getIden() {
		return iden;
	}
	
	public BufferedImage getImage() {
		return image;
	}
}
