// $COVERAGE-OFF$
package org.mulesoft.language.client.js

import upickle.Js

object CustomPicklerConfig extends upickle.AttributeTagged {
  override implicit def OptionW[T: Writer]: Writer[Option[T]] = Writer {
    case None => Js.Null
    case Some(s) => implicitly[Writer[T]].write(s)
  }

  override implicit def OptionR[T: Reader]: Reader[Option[T]] = Reader {
    case Js.Null => None
    case v: Js.Value => Some(implicitly[Reader[T]].read.apply(v))
  }
}

// $COVERAGE-ON$