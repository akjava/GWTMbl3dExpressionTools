package com.akjava.mbl3d.client;

import javax.annotation.Nullable;

public interface Mbl3dExpressionReceiver {
	public void receive(@Nullable Mblb3dExpression expression,boolean overwrite) ;
}
