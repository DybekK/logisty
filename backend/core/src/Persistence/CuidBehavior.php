<?php

namespace Core\Persistence;

/**
 * @template T
 */
interface CuidBehavior
{
    /**
     * @return T
     */
    public static function random();
}
