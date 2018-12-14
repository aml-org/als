package org.mulesoft.typesystem.nominal_types;

trait Injector {
  def inject(a: Adaptable): Unit
}
