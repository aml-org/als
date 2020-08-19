package org.mulesoft.amfintegration

import org.yaml.model.{YMap, YMapEntry, YNode}

object YPartImplicits {
  implicit class YNodeImplicits(yNode: YNode) {
    def withKey(k: String): YNode =
      YNode(YMap(IndexedSeq(YMapEntry(YNode(k), yNode)), yNode.sourceName))
  }
}
