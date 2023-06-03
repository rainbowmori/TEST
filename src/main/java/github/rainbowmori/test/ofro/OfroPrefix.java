package github.rainbowmori.test.ofro;

import github.rainbowmori.rainbowapi.util.PrefixUtil;
import github.rainbowmori.rainbowapi.util.Util;

/**
 * 不変的なプレフィックス
 */
public class OfroPrefix {

  //Teleport
  public static final PrefixUtil TP = new PrefixUtil(Util.mm("<gold>[<aqua>Ofro<light_purple>TP<gold>]"));

  //なんかlogとか
  public static final PrefixUtil OFRO = new PrefixUtil(Util.mm("<gold>[<aqua>Ofro<gold>]"));

  //BroadCast
  public static final PrefixUtil BC = new PrefixUtil(Util.mm("<gold>[<aqua>Ofro<dark_red>BC<gold>]"));

  //銀行系のPrefix
  public static final PrefixUtil BANK = new PrefixUtil(Util.mm("<gold>[<aqua>Ofro<yellow>Bank<gold>]"));

  //Shop計の販売とかそこらへんで使用する
  public static final PrefixUtil SHOP = new PrefixUtil(Util.mm("<gold>[<aqua>Ofro<blue>SHOP<gold>]"));

}
