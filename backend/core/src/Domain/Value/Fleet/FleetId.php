<?php

namespace Core\Domain\Value\Fleet;

use Core\Domain\Value\ValueObject;
use Core\Persistence\CuidBehavior;
use Visus\Cuid2\Cuid2;

/**
 * @implements ValueObject<string, FleetId>
 * @implements CuidBehavior<FleetId>
 */
final readonly class FleetId implements ValueObject, CuidBehavior
{
    private function __construct(
        public string $value,
    ) {}

    /**
     * @return FleetId
     */
    public static function random()
    {
        return new self(new Cuid2());
    }

    /**
     * @param string $value
     * @return FleetId
     */
    public static function of($value): self
    {
        return new self($value);
    }

    /**
     * @param FleetId $object
     * @return bool
     */
    public function eq($object): bool
    {
        return $object instanceof FleetId && $object->value === $this->value;
    }
}
