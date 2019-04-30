package org.mulesoft.als.server.modules.common.interfaces

/**
  * Position in text.
  */
case class IPoint(
                   /**
                     * Row number, starting from 0
                     */
                   var row: Int,

                   /**
                     * Column number, starting from 0
                     */
                   var column: Int
                 ) {}
