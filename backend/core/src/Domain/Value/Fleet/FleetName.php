<?php

namespace Core\Domain\Value\Fleet;

use Core\Domain\Value\ValueObject;

/**
 * @implements ValueObject<string, FleetName>
 */
final readonly class FleetName implements ValueObject
{
    /**
     * @param string $value
     */
    private function __construct(
        public string $value,
    ) {}

    public static function of(mixed $value): self
    {
        return new self($value);
    }

    public function eq(mixed $object): bool
    {
        return $object instanceof FleetName && $object->value === $this->value;
    }
}
