package moe.plushie.rpg_framework.api.mail;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Date;

public interface IMailMessage {
    
    public IMailSystem getMailSystem();
    
    public GameProfile getSender();

    public GameProfile getReceiver();
    
    public Date getSendDateTime();

    public String getSubject();
    
    public String getMessageText();
    
    public NonNullList<ItemStack> getAttachments();
}
